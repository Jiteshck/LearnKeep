const axios = require("axios");

async function sendEmail(to, subject, text) {
    try {
        await axios.post(
            "https://api.brevo.com/v3/smtp/email",
            {
                sender: {
                    name: "LearnKeep",
                    email: process.env.EMAIL_USER
                },
                to: [{ email: to }],
                subject: subject,
                htmlContent: `
                    <h2>📚 LearnKeep</h2>
                    <p>${text}</p>
                    <p style="color:gray;font-size:12px">
                    If this was not you, ignore this email.
                    </p>
                `
            },
            {
                headers: {
                    "api-key": process.env.BREVO_API_KEY,
                    "Content-Type": "application/json"
                }
            }
        );

        console.log("✅ Email sent to:", to);

    } catch (error) {
        console.error("❌ Brevo Email Error:", error.response?.data || error.message);
    }
}

module.exports = sendEmail;