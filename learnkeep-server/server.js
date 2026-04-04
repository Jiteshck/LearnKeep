const express = require("express");
const mongoose = require("mongoose");
const cors = require("cors");
const jwt = require("jsonwebtoken");

require("dotenv").config();

const User = require("./models/User");
const authMiddleware = require("./auth");
const sendEmail = require("./emailService");
const otpGenerator = require("otp-generator");
const otpStore = {};
const app = express();
const bcrypt = require("bcrypt");
const JWT_SECRET = process.env.JWT_SECRET;

const {
    otpTemplate,
    resetOtpTemplate,
    welcomeTemplate,
    loginAlertTemplate,
    passwordChangedTemplate
} = require("./emailTemplates");

app.use(cors());
app.use(express.json());

/* ---------------- MongoDB Connection ---------------- */

mongoose.connect(process.env.MONGO_URI)
.then(()=>{
console.log("MongoDB Connected");
})
.catch(err=>{
console.log(err);
});

/* ---------------- Signup API ---------------- */

app.post("/signup", async (req,res)=>{
    const {name,email,password,otp} = req.body;
    if (!otpStore[email] ||
        otpStore[email].otp !== otp ||
        otpStore[email].expires < Date.now()) {
        return res.json({success:false, message:"Invalid or expired OTP"});
    }

    const hashedPassword = await bcrypt.hash(password, 10);
    const user = new User({
        name,
        email,
        password: hashedPassword
    });
    await user.save();
    delete otpStore[email];

    // 🎉 Welcome Email
    await sendEmail(email, "Welcome to LearnKeep 🎉", welcomeTemplate(name));
    res.json({success:true});
});

app.post("/send-signup-otp", async (req,res)=>{

    const {email} = req.body;

    const otp = otpGenerator.generate(6, {
        digits: true,
        lowerCaseAlphabets: false,
        upperCaseAlphabets: false,
        specialChars: false
    });

    otpStore[email] = {
        otp,
        expires: Date.now() + 5 * 60 * 1000 // 5 minutes
    };

    await sendEmail(email, "Verify Your Account", otpTemplate(otp));

    res.json({success:true});
});

/* ---------------- Login API ---------------- */

app.post("/login", async (req,res)=>{

    const {email,password} = req.body;

    const user = await User.findOne({email});
    if(!user) return res.json({success:false, message:"User not found"});

    const isMatch = await bcrypt.compare(password, user.password);
    if(!isMatch) return res.json({success:false});

    const token = jwt.sign(
        {id:user._id},
        process.env.JWT_SECRET,
        {expiresIn:"7d"}
    );

    // 🚨 Login Alert Email
    await sendEmail(email, "⚠️ Login Alert", loginAlertTemplate());

    res.json({
        success:true,
        token,
        name:user.name,
        email:user.email
    });
});

app.post("/send-login-otp", async (req,res)=>{

    const {email} = req.body;

    const otp = otpGenerator.generate(6, {
        digits: true,
        lowerCaseAlphabets: false,
        upperCaseAlphabets: false,
        specialChars: false
    });

    otpStore[email] = {
        otp,
        expires: Date.now() + 5 * 60 * 1000 // 5 minutes
    };

    await sendEmail(email, "Login Verification Code", otpTemplate(otp));

    res.json({success:true});
});

/* ---------------- Protected Route Example ---------------- */

app.get("/profile", authMiddleware, async (req,res)=>{


try{

    const user = await User.findById(req.user.id);

    res.json({
        name:user.name,
        email:user.email
    });

}catch(err){

    res.status(500).json({
        success:false
    });

}


});

app.post("/forgot-password-otp", async (req,res)=>{

    const {email} = req.body;

    const otp = otpGenerator.generate(6, {
        digits: true,
        lowerCaseAlphabets: false,
        upperCaseAlphabets: false,
        specialChars: false
    });

    otpStore[email] = {
        otp,
        expires: Date.now() + 5 * 60 * 1000 // 5 minutes
    };

    await sendEmail(email, "Reset Password Code", resetOtpTemplate(otp));

    res.json({success:true});
});

app.post("/reset-password", async (req,res)=>{

    const {email,otp,newPassword} = req.body;

    if (!otpStore[email] ||
        otpStore[email].otp !== otp ||
        otpStore[email].expires < Date.now()) {
        return res.json({success:false, message:"Invalid or expired OTP"});
    }

    const hashedPassword = await bcrypt.hash(newPassword, 10);

    await User.updateOne(
        {email},
        {password: hashedPassword}
    );

    delete otpStore[email];
    await sendEmail(email, "Password Changed", passwordChangedTemplate());
    res.json({success:true});
});

/* ---------------- Server Start ---------------- */

const PORT = process.env.PORT || 3000;
app.listen(PORT, ()=>{
console.log("Server running on port 3000");
});
