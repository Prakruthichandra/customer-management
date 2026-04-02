import { Customer, CustomerRequest } from '../types/Customer';

export const fetchCustomers = async (): Promise<Customer[]> => {
  const response = await fetch('/api/v1/customers');

  if (!response.ok) {
    throw new Error(`Failed to fetch customers: ${response.status} ${response.statusText}`);
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
    throw new Error(`Failed to create customer: ${response.status} ${response.statusText}`);
  }

  return response.json();
};
