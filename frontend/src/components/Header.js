import React from 'react';
import { Link } from 'react-router-dom';
import '../styles/Header.css';

const Header = ({ isLoggedIn, handleLogout }) => {
    return (
        <header className="header">
            <nav>
                <ul>
                    <li><Link to="/">Home</Link></li>
                    {isLoggedIn && <li><Link to="/tests">Tests</Link></li>}
                    {isLoggedIn ? (
                        <li><button onClick={handleLogout}>Logout</button></li>
                    ) : (
                        <>
                            <li><Link to="/login">Login</Link></li>
                            <li><Link to="/register">Register</Link></li>
                        </>
                    )}
                </ul>
            </nav>
        </header>
    );
};

export default Header;