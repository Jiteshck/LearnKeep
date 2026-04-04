function otpTemplate(otp) {
    return `
    <div style="font-family:Arial;padding:20px;background:#F3F4F9">
        <h2 style="color:#3B28CC;">📚 LearnKeep</h2>
        <h3>Your Verification Code</h3>
        <div style="font-size:28px;font-weight:bold;color:#111;">
            ${otp}
        </div>
        <p>This code will expire in 5 minutes.</p>
        <p style="color:gray;">Do not share this code.</p>
    </div>`;
}

function resetOtpTemplate(otp) {
    return `
    <div style="font-family:Arial;padding:20px;background:#F3F4F9">
        <h2 style="color:#EF4444;">🔐 Reset Password</h2>
        <p>Your password reset code:</p>
        <div style="font-size:28px;font-weight:bold;">
            ${otp}
        </div>
        <p>This code expires in 5 minutes.</p>
    </div>`;
}

function welcomeTemplate(name) {
    return `
    <div style="font-family:Arial;padding:20px;background:#F3F4F9">
        <h2 style="color:#3B28CC;">🎉 Welcome to LearnKeep</h2>
        <p>Hi ${name},</p>
        <p>Your account has been successfully created.</p>
        <p>Start tracking your knowledge and grow smarter every day 🚀</p>
    </div>`;
}

function loginAlertTemplate() {
    return `
    <div style="font-family:Arial;padding:20px;background:#FFF3F3">
        <h2 style="color:#DC2626;">⚠️ Security Alert</h2>
        <p>Your LearnKeep account was just logged in.</p>
        <p>If this wasn't you, please reset your password immediately.</p>
    </div>`;
}

function passwordChangedTemplate() {
    return `
    <div style="font-family:Arial;padding:20px;background:#F3F4F9">
        <h2 style="color:#16A34A;">✅ Password Updated</h2>
        <p>Your password has been successfully changed.</p>
        <p>If this wasn't you, contact support immediately.</p>
    </div>`;
}

module.exports = {
    otpTemplate,
    resetOtpTemplate,
    welcomeTemplate,
    loginAlertTemplate,
    passwordChangedTemplate
};