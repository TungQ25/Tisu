import jwt from "jsonwebtoken";
import User from "../models/user.js";

export const authenticateToken = async (req, res, next) => {
    try {
        // Lấy token trong header
        const authHeader = req.headers['authorization'];
        const token = authHeader && authHeader.split(' ')[1];
        if (!token) return res.sendStatus(401); // Unauthorized

        // Xác minh token
        jwt.verify(token, process.env.ACCESS_TOKEN_SECRET, async (err, decoded) => {
            if (err) {
                console.error(err);
                return res.status(401).json({ message: "Invalid token" }); // Unauthorized
            }

            const user = await User.findOne({ account: decoded.accountId }).populate('account', 'username');
            if (!user) {
                return res.status(401).json({ message: "User not found" }); // Unauthorized
            }

            req.user = user;
            next();
        });
    } catch (error) {
        console.error('Lỗi khi xác minh JWT trong authMiddleware:', error);
        res.status(500).json({ message: "Server error" });
    }
};