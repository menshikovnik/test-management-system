import React, {useEffect, useState} from 'react';
import axios from '../utils/axiosConfig';
import {Button, Card, Col, Collapse, Container, Form, ListGroup, Row} from 'react-bootstrap';
import '../styles/Tests.css';

const Tests = () => {
    const [tests, setTests] = useState([]);
    const [showCreateTest, setShowCreateTest] = useState(false);
    const [expandedTests, setExpandedTests] = useState([]);
    const [newTest, setNewTest] = useState({
        name: '',
        questions: [{text: '', answers: [{text: '', correct: false}]}]
    });

    useEffect(() => {
        const fetchTests = () => {
            return axios.get('/tests/getAll');
        };

        fetchTests()
            .then(response => {
                setTests(response.data);
            })
            .catch(error => {
                console.error('Error fetching tests', error);
            });
    }, []);

    const handleCreateTestToggle = () => {
        setShowCreateTest(!showCreateTest);
    };

    const handleTestChange = (e) => {
        setNewTest({...newTest, [e.target.name]: e.target.value});
    };

    const handleQuestionChange = (index, e) => {
        const updatedQuestions = [...newTest.questions];
        updatedQuestions[index].text = e.target.value;
        setNewTest({...newTest, questions: updatedQuestions});
    };

    const handleAnswerChange = (qIndex, aIndex, e) => {
        const updatedQuestions = [...newTest.questions];
        updatedQuestions[qIndex].answers[aIndex][e.target.name] = e.target.value;
        setNewTest({...newTest, questions: updatedQuestions});
    };

    const handleAnswerCorrectChange = (qIndex, aIndex) => {
        const updatedQuestions = [...newTest.questions];
        updatedQuestions[qIndex].answers[aIndex].correct = !updatedQuestions[qIndex].answers[aIndex].correct;
        setNewTest({...newTest, questions: updatedQuestions});
    };

    const addQuestion = () => {
        setNewTest({
            ...newTest,
            questions: [...newTest.questions, {text: '', answers: [{text: '', correct: false}]}]
        });
    };

    const addAnswer = (index) => {
        const updatedQuestions = [...newTest.questions];
        updatedQuestions[index].answers.push({text: '', correct: false});
        setNewTest({...newTest, questions: updatedQuestions});
    };

    const handleCreateTest = async () => {
        try {
            await axios.post('/tests/create', newTest);
            alert('Test created successfully');
            setShowCreateTest(false);
            setNewTest({
                name: '',
                questions: [{text: '', answers: [{text: '', correct: false}]}]
            });

            const response = await axios.get('/tests');
            setTests(response.data);
        } catch (error) {
            console.error('Error creating test', error);
        }
    };

    const toggleTestExpansion = (testId) => {
        setExpandedTests((prevExpandedTests) =>
            prevExpandedTests.includes(testId)
                ? prevExpandedTests.filter((id) => id !== testId)
                : [...prevExpandedTests, testId]
        );
    };

    return (
        <Container className="tests mt-5">
            <h2>Tests</h2>
            <Button variant="primary" onClick={handleCreateTestToggle}>
                {showCreateTest ? 'Cancel' : 'Create Test'}
            </Button>
            <Collapse in={showCreateTest}>
                <div className="create-test mt-4">
                    <Card>
                        <Card.Header>Create New Test</Card.Header>
                        <Card.Body>
                            <Form>
                                <Form.Group controlId="formTestName">
                                    <Form.Label>Test Name</Form.Label>
                                    <Form.Control
                                        type="text"
                                        name="name"
                                        placeholder="Enter test name"
                                        value={newTest.name}
                                        onChange={handleTestChange}
                                    />
                                </Form.Group>
                                {newTest.questions.map((question, qIndex) => (
                                    <div key={qIndex} className="question mt-3">
                                        <Form.Group controlId={`formQuestionText${qIndex}`}>
                                            <Form.Label>Question {qIndex + 1}</Form.Label>
                                            <Form.Control
                                                type="text"
                                                placeholder="Enter question text"
                                                value={question.text}
                                                onChange={(e) => handleQuestionChange(qIndex, e)}
                                            />
                                        </Form.Group>
                                        {question.answers.map((answer, aIndex) => (
                                            <Row key={aIndex} className="answer mt-2">
                                                <Col>
                                                    <Form.Control
                                                        type="text"
                                                        name="text"
                                                        placeholder="Enter answer text"
                                                        value={answer.text}
                                                        onChange={(e) => handleAnswerChange(qIndex, aIndex, e)}
                                                    />
                                                </Col>
                                                <Col xs="auto">
                                                    <Form.Check
                                                        type="checkbox"
                                                        label="Correct"
                                                        checked={answer.correct}
                                                        onChange={() => handleAnswerCorrectChange(qIndex, aIndex)}
                                                    />
                                                </Col>
                                            </Row>
                                        ))}
                                        <Button variant="secondary" className="mt-2 add-answer-button"
                                                onClick={() => addAnswer(qIndex)}>
                                            Add Answer
                                        </Button>
                                    </div>
                                ))}
                                <Button variant="secondary" className="mt-3 add-question-button" onClick={addQuestion}>
                                    Add Question
                                </Button>
                                <Button variant="primary" className="mt-3 save-test" onClick={handleCreateTest}>
                                    Save Test
                                </Button>
                            </Form>
                        </Card.Body>
                    </Card>
                </div>
            </Collapse>
            <div className="test-list mt-4">
                {tests.map((test) => (
                    <Card key={test.id} className="test mb-3">
                        <Card.Header onClick={() => toggleTestExpansion(test.id)} style={{cursor: 'pointer'}}>
                            {test.name} {expandedTests.includes(test.id) ? '▼' : '►'}
                        </Card.Header>
                        <Collapse in={expandedTests.includes(test.id)}>
                            <div className="questions">
                                <ListGroup>
                                    {test.questions.map((question) => (
                                        <ListGroup.Item key={question.id} className="question">
                                            <strong>{question.text}</strong>
                                            <ul>
                                                {question.answers.map((answer) => (
                                                    <li key={answer.id} className={answer.correct ? 'correct' : ''}>
                                                        {answer.text}
                                                    </li>
                                                ))}
                                            </ul>
                                        </ListGroup.Item>
                                    ))}
                                </ListGroup>
                            </div>
                        </Collapse>
                    </Card>
                ))}
            </div>
        </Container>
    );
};

export default Tests;