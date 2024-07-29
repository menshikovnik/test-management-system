import React, { useState } from 'react';
import axios from 'axios';
import '../styles/Register.css';

const Register = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');

    const handleRegister = async () => {
        try {
            const response = await axios.post('http://localhost:8081/api/register', {
                email,
                password
            }, {
                headers: {
                    'Content-Type': 'application/json'
                }
            });
            if (response.status === 201) {
                alert('Registered successfully');
            } else {
                alert('Registration failed');
            }
        } catch (error) {
            if (error.response) {
                const { status, data } = error.response;

                if (status === 400 && typeof data === 'object') {
                    const errorMessages = Object.values(data).join('\n');
                    alert(`Validation errors:\n${errorMessages}`);
                } else {
                    alert(`Error: ${data}`);
                }
            } else if (error.request) {
                alert('No response received from server');
            } else {
                alert('Error setting up request');
            }
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