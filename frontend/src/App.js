import React, { useState } from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Header from './components/Header';
import Register from './pages/Register';
import Login from './pages/Login';
import Tests from './pages/Tests';
import Home from './pages/Home';
import './styles/App.css';

const App = () => {
    const [isLoggedIn, setIsLoggedIn] = useState(false);

    const handleLogin = () => {
        setIsLoggedIn(true);
    };

    const handleLogout = () => {
        setIsLoggedIn(false);
    };

    return (
        <Router>
            <Header isLoggedIn={isLoggedIn} handleLogout={handleLogout} />
            <Routes>
                <Route path="/" element={<Home isLoggedIn={isLoggedIn} />} />
                <Route path="/register" element={<Register onRegister={handleLogin} />} />
                <Route path="/login" element={<Login onLogin={handleLogin} />} />
                {isLoggedIn && <Route path="/tests" element={<Tests />} />}
            </Routes>
        </Router>
    );
};

export default App;