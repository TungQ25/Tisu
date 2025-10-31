import mongoose from 'mongoose';

const tagSchema = new mongoose.Schema({
    name: { type: String, required: true, trim: true },
    icon: { type: String },
    userId: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true }
});

const Tag = mongoose.model('Tag', tagSchema);

export default Tag;