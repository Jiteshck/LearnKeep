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
    if(otpStore[email] !== otp){
        return res.json({success:false, message:"Invalid OTP"});
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
    await sendEmail(email, "Welcome to LearnKeep 🎉",
        "Your account has been successfully created!");
    res.json({success:true});
});

app.post("/send-signup-otp", async (req,res)=>{

    const {email} = req.body;

    const otp = otpGenerator.generate(6, {
        digits:true,
        alphabets:false,
        upperCase:false,
        specialChars:false
    });

    otpStore[email] = otp;

    await sendEmail(email, "LearnKeep OTP", `Your OTP is ${otp}`);

    res.json({success:true});
});

/* ---------------- Login API ---------------- */

app.post("/login", async (req,res)=>{

    const {email,password,otp} = req.body;

    const user = await User.findOne({email});

    if(!user) return res.json({success:false});

    const isMatch = await bcrypt.compare(password, user.password);

    if(!isMatch) return res.json({success:false});

    if(otpStore[email] !== otp){
        return res.json({success:false, message:"Invalid OTP"});
    }

    delete otpStore[email];

    const token = jwt.sign(
        {id:user._id},
        process.env.JWT_SECRET,
        {expiresIn:"7d"}
    );

    // 🚨 Login Alert Email
    await sendEmail(email, "Login Alert ⚠️",
        "Your LearnKeep account was just accessed.");

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
        digits:true,
        alphabets:false
    });

    otpStore[email] = otp;

    await sendEmail(email, "Login OTP", `Your OTP is ${otp}`);

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
        digits:true
    });

    otpStore[email] = otp;

    await sendEmail(email, "Reset Password OTP", `OTP: ${otp}`);

    res.json({success:true});
});

app.post("/reset-password", async (req,res)=>{

    const {email,otp,newPassword} = req.body;

    if(otpStore[email] !== otp){
        return res.json({success:false});
    }

    const hashedPassword = await bcrypt.hash(newPassword, 10);

    await User.updateOne(
        {email},
        {password: hashedPassword}
    );

    delete otpStore[email];

    res.json({success:true});
});

/* ---------------- Server Start ---------------- */

const PORT = process.env.PORT || 3000;
app.listen(PORT, ()=>{
console.log("Server running on port 3000");
});
