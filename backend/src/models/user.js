import mongoose from 'mongoose';

const userSchema = new mongoose.Schema({
    account: { type: mongoose.Schema.Types.ObjectId, ref: 'Account', required: true },
    firstName: { type: String, required: true, trim: true },
    lastName: { type: String, required: true, trim: true },
    dateOfBirth: { type: Date },
    email: { type: String, required: true, unique: true, lowercase: true, trim: true },
    phone: { type: String, trim: true },
    address: { type: String, trim: true },
    avatarURL: { type: String },
    gender: { type: String, enum: ['male', 'female', 'other'] },
    isVerified: { type: Boolean, default: false },
    // bio: { type: String, default: null },
    createdAt: { type: Date, default: Date.now },
    updatedAt: { type: Date, default: Date.now },
    status: { type: String, enum: ['active', 'suspended', 'deleted'], default: 'active' }
});

const User = mongoose.model('User', userSchema);

export default User;