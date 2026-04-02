import React, { useState, useEffect } from 'react';
import { Customer } from '../types/Customer';
import { fetchCustomers } from '../services/customerApi';
import './CustomerList.css';

const CustomerList: React.FC = () => {
  const [customers, setCustomers] = useState<Customer[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [sortField, setSortField] = useState('lastName');
  const [sortDirection, setSortDirection] = useState<'asc' | 'desc'>('asc');

  const loadCustomers = async (page: number = 0) => {
    setLoading(true);
    setError(null);

    try {
      const response = await fetchCustomers(page, 5, sortField, sortDirection);
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
    loadCustomers(0);
  }, [sortField, sortDirection]);

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

  const handleSort = (field: string) => {
    // Reset to first page when sorting changes
    setCurrentPage(0);
    if (sortField === field) {
      // Toggle direction if same field
      setSortDirection(sortDirection === 'asc' ? 'desc' : 'asc');
    } else {
      // New field, default to ascending
      setSortField(field);
      setSortDirection('asc');
    }
  };

  if (loading) {
    return <div className="loading-state">Loading...</div>;
  }

  if (error) {
    return <div className="error-state">{error}</div>;
  }

  if (customers.length === 0) {
    return <div className="empty-state">No customers found</div>;
  }

  return (
    <div className="customer-list">
      <table className="customer-table">
        <thead>
          <tr>
            <th>S.No</th>
            <th className="sortable" onClick={() => handleSort('firstName')}>
              First Name
              {sortField === 'firstName' && (
                <span className="sort-icon">{sortDirection === 'asc' ? ' ▲' : ' ▼'}</span>
              )}
            </th>
            <th className="sortable" onClick={() => handleSort('lastName')}>
              Last Name
              {sortField === 'lastName' && (
                <span className="sort-icon">{sortDirection === 'asc' ? ' ▲' : ' ▼'}</span>
              )}
            </th>
            <th>Date of Birth</th>
          </tr>
        </thead>
        <tbody>
          {customers.map((customer, index) => (
            <tr key={customer.id}>
              <td>{currentPage * 5 + index + 1}</td>
              <td>{customer.firstName}</td>
              <td>{customer.lastName}</td>
              <td>{customer.dateOfBirth}</td>
            </tr>
          ))}
        </tbody>
      </table>

      <div className="pagination">
        <div className="pagination-info">
          <p>Page {currentPage + 1} of {totalPages}</p>
          <p>Total {totalElements} customers</p>
        </div>

        <div className="pagination-controls">
          <button className="pagination-button" onClick={handlePrevPage} disabled={currentPage === 0}>
            Previous
          </button>
          <button className="pagination-button" onClick={handleNextPage} disabled={currentPage >= totalPages - 1}>
            Next
          </button>
        </div>
      </div>
    </div>
  );
};

export default CustomerList;
