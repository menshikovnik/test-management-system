import React, {useEffect, useState} from 'react';
import {useNavigate, useParams} from 'react-router-dom';
import {Button, Container, Form} from 'react-bootstrap';
import axios from '../utils/axiosConfig';

const TestComponent = () => {
    const [isStarted, setIsStarted] = useState(false);
    const {token} = useParams();
    const [test, setTest] = useState(null);
    const [isTokenValid, setIsTokenValid] = useState(false);
    const navigate = useNavigate();
    const [userDetails, setUserDetails] = useState({
        name: '',
        surname: '',
        email: '',
    });
    const [answers, setAnswers] = useState({});
    const [error, setError] = useState(null);
    const [testResultId, setTestResultId] = useState(null);
    const [testResult, setTestResult] = useState(null);

    useEffect(() => {
        const validateToken = async () => {
            try {
                const response = await axios.get(`/invite/register/${token}`);
                if (response.status === 200) {
                    setIsTokenValid(true);
                    if (response.data.result != null) {
                        setTestResult(response.data.result);
                    } else if (response.data.id) {
                        setTestResultId(response.data.id);
                        setTest(response.data);

                        const initialAnswers = {};
                        response.data.questions.forEach(question => {
                            if (question.selectedAnswer) {
                                initialAnswers[question.id] = question.selectedAnswer;
                            }
                        });
                        setAnswers(initialAnswers);
                        setIsStarted(true);
                    }
                }
            } catch (error) {
                console.error('Error validating token:', error);
                setError('Invalid or expired invite link');
                navigate('/');
            }
        };

        validateToken();
    }, [token, navigate]);

    const handleInputChange = (e) => {
        const {name, value} = e.target;
        setUserDetails((prevDetails) => ({
            ...prevDetails,
            [name]: value,
        }));
    };

    const handleAnswerChange = (questionId, answer) => {
        setAnswers((prevAnswers) => ({
            ...prevAnswers,
            [questionId]: answer,
        }));
    };

    const handleStartTest = async () => {
        try {
            const response = await axios.post(`/invite/start-test/${token}`, userDetails);
            setTest(response.data);
            setIsStarted(true);
            setTestResultId(response.data.id);
        } catch (err) {
            console.error('Error starting test:', err);
            setError('Failed to start the test. Please try again.');
        }
    };

    const handleSaveAnswer = async (questionId) => {
        try {
            const selectedAnswer = answers[questionId]?.id;
            await axios.post(`/invite/partial-save/question/${questionId}`, {
                answer: selectedAnswer,
                token
            });
            alert('Answer saved successfully');
        } catch (err) {
            console.error('Error saving answer:', err);
            setError('Failed to save the answer. Please try again.');
        }
    };

    const handleSubmitTest = async () => {
        try {
            const formattedAnswers = {};
            Object.keys(answers).forEach(questionId => {
                formattedAnswers[questionId] = answers[questionId].id || answers[questionId];
            });

            await axios.post(`/invite/submit-test/${token}`, {
                userDetails,
                answers: formattedAnswers,
            });
            alert('Test submitted successfully');
        } catch (err) {
            console.error('Error submitting test:', err);
            setError('Failed to submit the test. Please try again.');
        }
    };

    return (
        <Container className="invite-test-page mt-5">
            {testResult !== null ? (
                <div className="test-result">
                    <h2>Test Completed</h2>
                    <p>Your result: {testResult}%</p>
                </div>
            ) : (
                !isStarted && isTokenValid ? (
                    <Form>
                        <h2>Enter your details to start the test</h2>
                        <Form.Group>
                            <Form.Label>First Name</Form.Label>
                            <Form.Control
                                type="text"
                                name="name"
                                value={userDetails.name}
                                onChange={handleInputChange}
                            />
                        </Form.Group>
                        <Form.Group>
                            <Form.Label>Last Name</Form.Label>
                            <Form.Control
                                type="text"
                                name="surname"
                                value={userDetails.surname}
                                onChange={handleInputChange}
                            />
                        </Form.Group>
                        <Form.Group>
                            <Form.Label>Email</Form.Label>
                            <Form.Control
                                type="email"
                                name="email"
                                value={userDetails.email}
                                onChange={handleInputChange}
                            />
                        </Form.Group>
                        <Button onClick={handleStartTest}>Start Test</Button>
                        {error && <p className="text-danger">{error}</p>}
                    </Form>
                ) : (
                    <div className="test-content">
                        {test && (
                            <>
                                <h2>{test.name}</h2>
                                <Form>
                                    {test.questions.map((question) => (
                                        <div key={question.id} className="mb-3">
                                            <h4>{question.text}</h4>
                                            {question.answers.map((answer) => (
                                                <Form.Check
                                                    key={answer.id}
                                                    type="radio"
                                                    label={answer.text}
                                                    name={`question-${question.id}`}
                                                    checked={answers[question.id]?.id === answer.id}
                                                    onChange={() => handleAnswerChange(question.id, answer)}
                                                />
                                            ))}
                                            <Button
                                                className="mt-2"
                                                onClick={() => handleSaveAnswer(question.id)}
                                            >
                                                Save Answer
                                            </Button>
                                        </div>
                                    ))}
                                    <Button onClick={handleSubmitTest}>Submit Test</Button>
                                </Form>
                            </>
                        )}
                        {error && <p className="text-danger">{error}</p>}
                    </div>
                )
            )}
        </Container>
    );
};

export default TestComponent;