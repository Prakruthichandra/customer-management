import React, { useState, useEffect } from 'react';
import { Customer } from '../types/Customer';
import { fetchCustomers } from '../services/customerApi';

const CustomerList: React.FC = () => {
  const [customers, setCustomers] = useState<Customer[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  const loadCustomers = async (page: number = 0) => {
    setLoading(true);
    setError(null);

    try {
      const response = await fetchCustomers();
      setCustomers(response.customers);
      setCurrentPage(response.page);
      setTotalPages(response.totalPages);
      setTotalElements(response.totalElements);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to fetch customers');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadCustomers();
  }, []);

  const handleNextPage = () => {
    if (currentPage < totalPages - 1) {
      loadCustomers(currentPage + 1);
    }
  };

  const handlePrevPage = () => {
    if (currentPage > 0) {
      loadCustomers(currentPage - 1);
    }
  };

  if (loading) {
    return <div>Loading...</div>;
  }

  if (error) {
    return <div>{error}</div>;
  }

  if (customers.length === 0) {
    return <div>No customers found</div>;
  }

  return (
    <div>
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>First Name</th>
            <th>Last Name</th>
            <th>Date of Birth</th>
          </tr>
        </thead>
        <tbody>
          {customers.map((customer) => (
            <tr key={customer.id}>
              <td>{customer.id}</td>
              <td>{customer.firstName}</td>
              <td>{customer.lastName}</td>
              <td>{customer.dateOfBirth}</td>
            </tr>
          ))}
        </tbody>
      </table>

      <div>
        <p>Page {currentPage + 1} of {totalPages}</p>
        <p>Total {totalElements} customers</p>
      </div>

      <div>
        <button onClick={handlePrevPage} disabled={currentPage === 0}>
          Previous
        </button>
        <button onClick={handleNextPage} disabled={currentPage >= totalPages - 1}>
          Next
        </button>
      </div>
    </div>
  );
};

export default CustomerList;
