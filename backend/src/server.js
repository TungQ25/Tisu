import dotenv from 'dotenv';
import express from 'express';
import connectDB from './libs/db.js';
import authRoute from './routers/authRoute.js';
// import './routers/userRoute.js';
// import './routers/taskGroupRoute.js';
// import './routers/taskRoute.js';
// import './routers/noteRoute.js';
// import './routers/eventRoute.js';
// import './routers/reminderRoute.js';
// import './routers/tagRoute.js';

dotenv.config();

const app = express();
const PORT = process.env.PORT || 5001;

// Middleware
app.use(express.json());

// public routes
app.use('/api/auth', authRoute);

// private routes
// app.use('/api/users', userRoute);
// app.use('/api/task-groups', taskGroupRoute);
// app.use('/api/tasks', taskRoute);
// app.use('/api/notes', noteRoute);
// app.use('/api/events', eventRoute);
// app.use('/api/reminders', reminderRoute);
// app.use('/api/tags', tagRoute);

connectDB().then(() => {
    app.listen(PORT, () => {
        console.log(`Server is running on port ${PORT}`);
    });
});