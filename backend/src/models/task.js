import mongoose from 'mongoose';

const taskSchema = new mongoose.Schema({
    title: { type: String, required: true, trim: true },
    dueDate: { type: Date },
    description: { type: String },
    status: { type: String, enum: ['pending', 'done'], default: 'pending' },    
    priority: { type: String, enum: ['none', 'low', 'medium', 'high'], default: 'none' },
    taskGroupId: { type: mongoose.Schema.Types.ObjectId, ref: 'TaskGroup', required: true },
    tagId: { type: mongoose.Schema.Types.ObjectId, ref: 'Tag' },
    userId: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true }
});

const Task = mongoose.model('Task', taskSchema);

export default Task;