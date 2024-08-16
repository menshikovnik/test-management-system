import React from 'react';
import {Container, Dropdown, Nav, Navbar} from 'react-bootstrap';
import '../styles/Header.css';

const Header = ({isLoggedIn, handleLogout}) => {
    return (
        <Navbar className="custom-navbar" bg="light" expand="lg">
            <Container>
                <Navbar.Brand href="/" className="custom-brand">Test Management System</Navbar.Brand>
                <Navbar.Toggle aria-controls="basic-navbar-nav"/>
                <Navbar.Collapse id="basic-navbar-nav">
                    <Nav className="ms-auto">
                        <Dropdown align="end">
                            <Dropdown.Toggle className="custom-dropdown-toggle" id="dropdown-basic">
                                Menu
                            </Dropdown.Toggle>
                            <Dropdown.Menu className="custom-dropdown-menu">
                                <Dropdown.Item href="/" className="custom-dropdown-item">Home</Dropdown.Item>
                                {isLoggedIn &&
                                    <Dropdown.Item href="/tests" className="custom-dropdown-item">Tests</Dropdown.Item>}
                                <Dropdown.Item href="/tests/results"
                                               className="custom-dropdown-item">Test Results</Dropdown.Item>
                                {isLoggedIn ? (
                                    <>
                                        <Dropdown.Item onClick={handleLogout}
                                                       className="custom-dropdown-item">Logout</Dropdown.Item>
                                        <Dropdown.Item href="/profile"
                                                       className="custom-dropdown-item">Profile</Dropdown.Item>
                                    </>
                                ) : (
                                    <>
                                        <Dropdown.Item href="/login"
                                                       className="custom-dropdown-item">Login</Dropdown.Item>
                                        <Dropdown.Item href="/register"
                                                       className="custom-dropdown-item">Register</Dropdown.Item>
                                    </>
                                )}
                            </Dropdown.Menu>
                        </Dropdown>
                    </Nav>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
};

export default Header;