import { Customer, CustomerRequest, PagedResponse } from '../types/Customer';

export const fetchCustomers = async (): Promise<PagedResponse<Customer>> => {
  const response = await fetch('/api/v1/customers');

  if (!response.ok) {
    const error = await response.json();
    throw new Error(error.message || 'Failed to fetch customers');
  }

  return response.json();
};

export const createCustomer = async (customer: CustomerRequest): Promise<Customer> => {
  const response = await fetch('/api/v1/customers', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(customer),
  });

  if (!response.ok) {
    const error = await response.json();
    throw new Error(error.message || 'Failed to create customer');
  }

  return response.json();
};
