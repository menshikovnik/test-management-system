import React, {useEffect, useState} from 'react';
import axios from '../utils/axiosConfig';
import '../styles/Tests.css';

const Tests = () => {
    const [tests, setTests] = useState([]);

    useEffect(() => {
        const fetchTests = async () => {
            try {
                const response = await axios.get('/api/tests');
                setTests(response.data);
            } catch (error) {
                console.error('Error fetching tests', error);
            }
        };

        fetchTests();
    }, []);

    return (
        <div className="tests">
            <h2>Tests</h2>
            <ul>
                {tests.map((test) => (
                    <li key={test.id}>{test.name}</li>
                ))}
            </ul>
        </div>
    );
};

export default Tests;