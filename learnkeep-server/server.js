const express = require("express");
const mongoose = require("mongoose");
const cors = require("cors");
const jwt = require("jsonwebtoken");

require("dotenv").config();

const User = require("./models/User");
const authMiddleware = require("./auth");

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

    const {name,email,password} = req.body;

    try{

        const existing = await User.findOne({email});

        if(existing){
            return res.json({success:false});
        }

        // 🔐 HASH PASSWORD
        const hashedPassword = await bcrypt.hash(password, 10);

        const user = new User({
            name,
            email,
            password: hashedPassword
        });

        await user.save();

        res.json({success:true});

    }catch(err){
        res.status(500).json({success:false});
    }

});

/* ---------------- Login API ---------------- */

app.post("/login", async (req,res)=>{
    const {email,password} = req.body;
    try{
        const user = await User.findOne({email});
        if(!user){
            return res.json({success:false});
        }
        // 🔐 COMPARE HASH
        const isMatch = await bcrypt.compare(password, user.password);
        if(!isMatch){
            return res.json({success:false});
        }
        const token = jwt.sign(
            {id:user._id},
            process.env.JWT_SECRET,
            {expiresIn:"7d"}
        );
        res.json({
            success:true,
            name:user.name,
            email:user.email,
            token:token
        });
    }catch(err){
        res.status(500).json({success:false});
    }
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

/* ---------------- Server Start ---------------- */

const PORT = process.env.PORT || 3000;
app.listen(PORT, ()=>{
console.log("Server running on port 3000");
});
