import { Customer, CustomerRequest, PagedResponse } from '../types/Customer';

export const fetchCustomers = async (
  page: number = 0,
  size: number = 5,
  sort: string = 'lastName',
  direction: 'asc' | 'desc' = 'asc'
): Promise<PagedResponse<Customer>> => {
  const params = new URLSearchParams({
    page: page.toString(),
    size: size.toString(),
    sort: `${sort},${direction}`
  });

  const response = await fetch(`/api/v1/customers?${params}`);

  if (!response.ok) {
    try {
      const error = await response.json();
      throw new Error(error.message || 'Failed to fetch customers');
    } catch (e) {
      if (e instanceof Error && !e.message.includes('is not a function') && !e.message.includes('Unexpected token')) {
        throw e;
      }
      throw new Error(`Failed to fetch customers: ${response.status} ${response.statusText}`);
    }
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
    try {
      const error = await response.json();
      throw new Error(error.message || 'Failed to create customer');
    } catch (e) {
      if (e instanceof Error && !e.message.includes('is not a function') && !e.message.includes('Unexpected token')) {
        throw e;
      }
      throw new Error(`Failed to create customer: ${response.status} ${response.statusText}`);
    }
  }

  return response.json();
};
