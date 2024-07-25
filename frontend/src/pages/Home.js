import React from 'react';
import { Link } from 'react-router-dom';
import '../styles/Home.css';

const Home = () => {
    return (
        <div className="home">
            <h1>Welcome to Test Management System</h1>
            <p>Manage your tests efficiently and effortlessly with our Test Management System. Sign up to get started or log in if you already have an account.</p>
            <Link to="/register">Get Started</Link>
        </div>
    );
};

export default Home;