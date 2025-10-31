import mongoose from 'mongoose';

const eventSchema = new mongoose.Schema({
    title: { type: String, required: true, trim: true },
    startTime: { type: Date, required: true },
    endTime: { type: Date },
    location: { type: String },
    description: { type: String },
    isAllDay: { type: Boolean, default: false },
    color: { type: String },
    userId: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
    calendarId: { type: mongoose.Schema.Types.ObjectId, ref: 'Calendar' },
    reminderId: { type: mongoose.Schema.Types.ObjectId, ref: 'Reminder' }
});

const Event = mongoose.model('Event', eventSchema);

export default Event;