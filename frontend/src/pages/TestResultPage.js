import React, {useEffect, useState} from 'react';
import {useParams} from 'react-router-dom';
import axios from '../utils/axiosConfig';
import {Container, Table} from 'react-bootstrap';

const TestResultsPage = () => {
    const {userId} = useParams();
    const [results, setResults] = useState([]);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchResults = async () => {
            try {
                let userId = localStorage.getItem('user')
                const response = await axios.get(`/tests/results/${userId}/`);
                setResults(response.data);
            } catch (err) {
                console.error('Error fetching test results:', err);
                setError('Failed to load test results.');
            }
        };

        fetchResults();
    }, [userId]);

    return (
        <Container className="mt-5">
            <h2>Test Results</h2>
            {error && <p className="text-danger">{error}</p>}
            <Table striped bordered hover>
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Surname</th>
                    <th>Email</th>
                    <th>Result (%)</th>
                    <th>Details</th>
                </tr>
                </thead>
                <tbody>
                {results.map((result, index) => (
                    <tr key={index}>
                        <td>{result.name}</td>
                        <td>{result.surname}</td>
                        <td>{result.email}</td>
                        <td>{result.result.toFixed(2)}</td>
                        <td>
                            <ul>
                                {result.questionResults.map((questionResult, idx) => (
                                    <li key={idx}>
                                        <strong>Question:</strong> {questionResult.question}<br/>
                                        <strong>Selected Answer:</strong> {questionResult.selectedAnswer} <br/>
                                        <strong>Correct:</strong>
                                        <span style={{color: questionResult.correct ? 'green' : 'red'}}>
                                            {questionResult.correct ? ' Yes' : ' No'}
                                        </span>
                                    </li>
                                ))}
                            </ul>
                        </td>
                    </tr>
                ))}
                </tbody>
            </Table>
        </Container>
    );
};

export default TestResultsPage;