// src/pages/Register.js

import React, { useState } from 'react';
import axios from 'axios';
import '../styles/Register.css';

const Register = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');

    const handleRegister = async () => {
        try {
            const response = await axios.post('/api/register', {
                email,
                password
            }, {
                headers: {
                    'Content-Type': 'application/json'
                }
            });
            alert('Registered successfully');
        } catch (error) {
            alert('Error registering');
        }
    };

    return (
        <div className="register">
            <div className="login-container">
                <h2>Register</h2>
                <input
                    type="email"
                    placeholder="Email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                />
                <input
                    type="password"
                    placeholder="Password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                />
                <button onClick={handleRegister}>Register</button>
            </div>
        </div>
    );
};

export default Register;