import mongoose from 'mongoose';

const taskGroupSchema = new mongoose.Schema({
    title: { type: String, required: true, trim: true },
    description: { type: String },
    color: { type: String },
    userId: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
    tagId: { type: mongoose.Schema.Types.ObjectId, ref: 'Tag' },
    createdAt: { type: Date, default: Date.now },
    updatedAt: { type: Date, default: Date.now }
});

const TaskGroup = mongoose.model('TaskGroup', taskGroupSchema);

export default TaskGroup;