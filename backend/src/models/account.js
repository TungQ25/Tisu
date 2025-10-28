import mongoose from 'mongoose';

const accountSchema = new mongoose.Schema({
    username: { 
        type: String, 
        required: true, 
        unique: true,
        trim: true,
        lowercase: true,
        minlength: [3, 'Username must be at least 3 characters long'],
        maxlength: [30, 'Username must be at most 30 characters long'],
    },
    password: { 
        type: String, required: true,
        minlength: [8, 'Password must be at least 8 characters long'],
        maxlength: [30, 'Password must be at most 30 characters long'],
    },
    email: { 
        type: String, required: true, unique: true,
        trim: true,
        lowercase: true,
        validate: {
            validator: function(v) {
                return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v);
            },
            message: 'Invalid email address',
        },
    },
    createdAt: { type: Date, default: Date.now },
    updatedAt: { type: Date, default: Date.now },
    isActive: { type: Boolean, default: true },
    isVerified: { type: Boolean, default: false },
    phoneNumber: { 
        type: String,
        sparse: true, // Cho phép null nhưng không được trùng
        unique: true,
        trim: true,
        // validate: {
        //     validator: function(v) {
        //         return /^[0-9]{10}$/.test(v);
        //     },
        //     message: 'Invalid phone number',
        // },
    },
    role: { type: String, enum: ['admin', 'user'], default: 'user' },
    firstName: { type: String, required: true, trim: true },
    lastName: { type: String, required: true, trim: true },
    avatarURL: { type: String},
    bio: { type: String, default: null },

});

const Account = mongoose.model('Account', accountSchema);

export default Account;