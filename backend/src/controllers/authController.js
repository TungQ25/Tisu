import Account from "../models/account.js";
import User from "../models/user.js";
import bcrypt from "bcrypt";
import jwt from "jsonwebtoken";
import crypto from "crypto";
import Session from "../models/session.js";

const ACCESS_TOKEN_TTL = '30m';
const REFRESH_TOKEN_TTL = 14 * 24 * 60 * 60 * 1000; // 14 days in milliseconds

export const registerUser = async (req, res) => {
    try {
        const { username, password, firstName, lastName, email } = req.body;
        // Validate input
        if (!username || !password || !firstName || !lastName || !email) {
            return res.status(400).json({ message: "Missing required fields" });
        }

        // Check if username or email already exists
        const existingAccount = await Account.findOne({ username });
        const existingUser = await User.findOne({ email });
        if (existingAccount || existingUser) {
            return res.status(400).json({ message: "Username or email already exists" });
        }

        // Hash password
        if (password.length < 6) {
            return res.status(400).json({ message: "Password must be at least 6 characters long" });
        }
        const hashedPassword = await bcrypt.hash(password, 10);

        // Create new account
        const newAccount = new Account({ username, hashedPassword, authProvider: "local" });
        const savedAccount = await newAccount.save();

        // Create new user
        const newUser = new User({ account: savedAccount._id, firstName, lastName, email });
        await newUser.save();
        res.status(201).json({ message: "User registered successfully" });
    } catch (error) {
        console.error(error);
        res.status(500).json({ message: "Server error" });
    }
};

export const loginUser = async (req, res) => {
    try {
        const { username, password } = req.body;
        // Validate input
        if (!username || !password) {
            return res.status(400).json({ message: "Missing username or password" });
        }

        // Find account by username
        const account = await Account.findOne({ username });
        if (!account) {
            return res.status(400).json({ message: "Invalid username or password" });
        }

        // Compare password
        const isPasswordValid = await bcrypt.compare(password, account.hashedPassword);
        if (!isPasswordValid) {
            return res.status(400).json({ message: "Invalid username or password" });
        }

        // Tạo JWT token
        const accessToken = jwt.sign({ accountId: account._id }, process.env.ACCESS_TOKEN_SECRET, { expiresIn: ACCESS_TOKEN_TTL });

        // Tạo refresh token ngẫu nhiên
        const refreshToken = crypto.randomBytes(64).toString('hex');

        // Tạo session mới để lưu refresh token
        const session = new Session({ userId: account._id, refreshToken, expiresAt: Date.now() + REFRESH_TOKEN_TTL });
        await session.save();

        // Trả refresh token về trong cookie
        res.cookie("refreshToken", refreshToken, { httpOnly: true, secure: true, sameSite: "none", maxAge: REFRESH_TOKEN_TTL });

        // Trả access token về trong response
        return res.status(200).json({ message: `User ${username} logged in successfully`, accessToken });
    } catch (error) {
        console.error(error);
        res.status(500).json({ message: "Server error" });
    }
};

export const logoutUser = async (req, res) => {
    try {
        // Xóa refresh token trong cookie và trong database
        const refreshToken = req.cookies?.refreshToken;
        if (!refreshToken) {
            return res.status(204).json({ message: 'No refresh token found' });
        }
        await Session.deleteOne({ refreshToken });
        res.clearCookie("refreshToken");
        res.status(200).json({ message: 'Logout successful' });
    } catch (error) {
        console.error(error);
        res.status(500).json({ message: 'Server error' });
    }
};