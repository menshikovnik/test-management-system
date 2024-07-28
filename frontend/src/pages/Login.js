import React, {useState} from 'react';
import axios from 'axios';
import '../styles/Login.css';

const Login = ({onLogin}) => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');

    const handleLogin = async () => {
        const response = await axios.post('http://localhost:8081/api/login', {
            email,
            password
        }, {
            headers: {
                'Content-Type': 'application/json'
            }
        });
        if (response.status === 200) {
            alert('Logged in successfully');
            onLogin();
        } else {
            alert('Error logging in');
        }
    };

    return (
        <div className="login">
            <div className="login-container">
                <h2>Login</h2>
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
                <button onClick={handleLogin}>Login</button>
                <p>Don't have an account? <a href="/register">Register</a></p>
            </div>
        </div>
    );
};

export default Login;