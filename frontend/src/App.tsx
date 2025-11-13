import { BrowserRouter, Route, Routes } from 'react-router';
import HomePage from './pages/HomePage';
import LoginPage from './pages/LoginPage';
import SignUpPage from './pages/SignUpPage';
import { Toaster } from 'sonner';

function App() {
  return <>
    <Toaster richColors />
    <BrowserRouter>
      <Routes>
        {/* Public routes */}
        <Route path="/" element={<HomePage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/signup" element={<SignUpPage />} />

        {/* Protected routes */}
        
        
      </Routes>
    </BrowserRouter>
  </>;
}

export default App;
