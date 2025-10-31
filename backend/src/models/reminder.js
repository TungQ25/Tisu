import mongoose from 'mongoose';

const reminderSchema = new mongoose.Schema({
    msg: { type: String, required: true, trim: true },
    targetId: { type: mongoose.Schema.Types.ObjectId, required: true },
    targetType: { type: String, enum: ['task', 'event', 'note', 'custom'], required: true },
    remindAt: { type: Date, required: true },
    status: { type: String, enum: ['scheduled', 'fired', 'dismissed'], default: 'scheduled' },
    repeatRule: { type: String },
    userId: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true }
});

const Reminder = mongoose.model('Reminder', reminderSchema);

export default Reminder;