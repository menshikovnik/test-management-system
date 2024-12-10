import React, {useEffect, useState} from 'react';
import {useParams} from 'react-router-dom';
import axios from '../utils/axiosConfig';
import {Container, Table, Button, Modal} from 'react-bootstrap';

const TestResultsPage = () => {
    const {userId} = useParams();
    const [results, setResults] = useState([]);
    const [error, setError] = useState(null);
    const [isAscendingByResult, setIsAscendingByResult] = useState(true);
    const [isAscendingByAge, setIsAscendingByAge] = useState(true);

    const [showModal, setShowModal] = useState(false);
    const [selectedResult, setSelectedResult] = useState(null);

    useEffect(() => {
        const fetchResults = async () => {
            try {
                let userId = localStorage.getItem('user');
                const response = await axios.get(`/tests/results/${userId}/`);
                setResults(response.data);
            } catch (err) {
                console.error('Error fetching test results:', err);
                setError('Failed to load test results.');
            }
        };

        fetchResults();
    }, [userId]);

    const handleSortByResult = () => {
        const sortedResults = [...results].sort((a, b) => {
            if (isAscendingByResult) {
                return a.result - b.result;
            } else {
                return b.result - a.result;
            }
        });
        setResults(sortedResults);
        setIsAscendingByResult(!isAscendingByResult);
    };

    const handleSortByAge = () => {
        const sortedResults = [...results].sort((a, b) => {
            if (isAscendingByAge) {
                return (a.age ?? 0) - (b.age ?? 0);
            } else {
                return (b.age ?? 0) - (a.age ?? 0);
            }
        });
        setResults(sortedResults);
        setIsAscendingByAge(!isAscendingByAge);
    };

    const handleShowModal = (result) => {
        setSelectedResult(result);
        setShowModal(true);
    };

    const handleCloseModal = () => {
        setSelectedResult(null);
        setShowModal(false);
    };

    const handleDelete = async () => {
        if (!selectedResult || !selectedResult.id) return;
        try {
            console.log('id', selectedResult.id)
            await axios.delete(`/tests/results/delete/${selectedResult.id}`);
            setResults((prevResults) => prevResults.filter((r) => r.id !== selectedResult.id));
        } catch (err) {
            console.error('Error deleting test result:', err);
            setError('Failed to delete the test result.');
        } finally {
            handleCloseModal();
        }
    };

    return (
        <Container className="mt-5">
            <h2>Test Results</h2>
            {error && <p className="text-danger">{error}</p>}

            <div className="mb-3">
                <Button variant="primary" onClick={handleSortByResult} className="me-2">
                    Sort by result ({isAscendingByResult ? 'ascending' : 'descending'})
                </Button>
                <Button variant="secondary" onClick={handleSortByAge}>
                    Sort by age ({isAscendingByAge ? 'ascending' : 'descending'})
                </Button>
            </div>

            <Table striped bordered hover>
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Surname</th>
                    <th>Age</th>
                    <th>Email</th>
                    <th>Result (%)</th>
                    <th>Details</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                {results.map((result, index) => (
                    <tr key={index}>
                        <td>{result.name}</td>
                        <td>{result.surname}</td>
                        <td>{result.age}</td>
                        <td>{result.email}</td>
                        <td>{result.result.toFixed(2)}</td>
                        <td>
                            {(() => {
                                const uniqueResults = [];
                                const seenQuestions = new Set();

                                for (const qr of result.questionResults) {
                                    if (!seenQuestions.has(qr.question)) {
                                        seenQuestions.add(qr.question);
                                        uniqueResults.push(qr);
                                    }
                                }

                                return (
                                    <ul>
                                        {uniqueResults.map((questionResult, idx) => (
                                            <li key={idx}>
                                                <strong>Question:</strong> {questionResult.question}<br/>
                                                <strong>Selected Answer:</strong> {questionResult.selectedAnswer}<br/>
                                                <strong>Correct:</strong>
                                                <span style={{color: questionResult.correct ? 'green' : 'red'}}>
                        {questionResult.correct ? ' Yes' : ' No'}
                    </span>
                                            </li>
                                        ))}
                                    </ul>
                                );
                            })()}

                        </td>
                        <td>
                            <Button variant="danger" onClick={() => handleShowModal(result)}>Delete</Button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </Table>

            {/* Modal for confirmation */}
            <Modal show={showModal} onHide={handleCloseModal}>
                <Modal.Header closeButton>
                    <Modal.Title>Are you sure?</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    {selectedResult && (
                        <p>
                            The record for {selectedResult.name} {selectedResult.surname} will be permanently deleted.
                            This action cannot be undone.
                        </p>
                    )}
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={handleCloseModal}>
                        Cancel
                    </Button>
                    <Button variant="danger" onClick={handleDelete}>
                        Confirm
                    </Button>
                </Modal.Footer>
            </Modal>
        </Container>
    );
};

export default TestResultsPage;
