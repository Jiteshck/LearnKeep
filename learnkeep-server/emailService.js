const sgMail = require("@sendgrid/mail");

sgMail.setApiKey(process.env.SENDGRID_API_KEY);

async function sendEmail(to, subject, text) {
    const msg = {
        to,
        from: process.env.EMAIL_USER,
        subject,
        text,
        html: `
            <h2>📚 LearnKeep</h2>
            <p>${text}</p>
            <p style="color:gray;font-size:12px">
            If this was not you, ignore this email.
            </p>
        `
    };

    try {
        await sgMail.send(msg);
        console.log("✅ Email sent to:", to);
    } catch (error) {
        console.error("❌ SendGrid error:", error.response?.body || error);
    }
}

module.exports = sendEmail;
