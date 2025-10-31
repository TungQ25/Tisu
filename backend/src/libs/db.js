import mongoose from 'mongoose';

const connectDB = async () => {
    try {
        await mongoose.connect(process.env.MONGODB_CONNECTIONSTRING);
        console.log('MongoDB connected successfully');
    } catch (error) {
        console.log('MongoDB connection error:', error);
        process.exit(1); // Exit with failure
    }
};

export default connectDB;