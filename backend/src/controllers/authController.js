import Account from "../models/account.js";
import User from "../models/user.js";
import bcrypt from "bcrypt";
import jwt from "jsonwebtoken";

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
        // Find account by username and check password
        const account = await Account.findOne({ username });
        const isPasswordValid = await bcrypt.compare(password, account.hashedPassword);
        if (!isPasswordValid || !account) {
            return res.status(400).json({ message: "Invalid username or password" });
        }

        // Generate JWT token
        const token = jwt.sign({ accountId: account._id }, process.env.JWT_SECRET, { expiresIn: "1d" });
        res.status(200).json({ token });
    } catch (error) {
        console.error(error);
        res.status(500).json({ message: "Server error" });
    }
};