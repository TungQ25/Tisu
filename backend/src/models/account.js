import mongoose from 'mongoose';

const accountSchema = new mongoose.Schema({
    username: { 
        type: String, 
        required: true, 
        unique: true,
        trim: true,
        lowercase: true,
        minlength: [3, 'Username must be at least 3 characters long'],
        maxlength: [30, 'Username must be at most 30 characters long']
    },
    hashedPassword: { 
        type: String, required: true
    },
    role: { type: String, enum: ['admin', 'user'], default: 'user' },
    authProvider: { type: String, enum: ['local', 'google', 'facebook'], default: 'local' },
    lastLogin: { type: Date },
    createdAt: { type: Date, default: Date.now },
    updatedAt: { type: Date, default: Date.now }
});

const Account = mongoose.model('Account', accountSchema);

export default Account;