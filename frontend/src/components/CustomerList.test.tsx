import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import CustomerList from './CustomerList';
import * as customerApi from '../services/customerApi';
import { PagedResponse } from '../types/Customer';

jest.mock('../services/customerApi');

describe('CustomerList', () => {
  const mockPagedResponse: PagedResponse<any> = {
    customers: [
      {
        id: '123e4567-e89b-12d3-a456-426614174000',
        firstName: 'John',
        lastName: 'Doe',
        dateOfBirth: '1990-01-15',
      },
      {
        id: '123e4567-e89b-12d3-a456-426614174001',
        firstName: 'Jane',
        lastName: 'Smith',
        dateOfBirth: '1985-05-20',
      },
    ],
    page: 0,
    size: 20,
    totalElements: 2,
    totalPages: 1,
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render loading state initially', () => {
    (customerApi.fetchCustomers as jest.Mock).mockReturnValue(new Promise(() => {}));

    render(<CustomerList />);

    expect(screen.getByText(/loading/i)).toBeInTheDocument();
  });

  it('should fetch and display customers on mount', async () => {
    (customerApi.fetchCustomers as jest.Mock).mockResolvedValue(mockPagedResponse);

    render(<CustomerList />);

    await waitFor(() => {
      expect(customerApi.fetchCustomers).toHaveBeenCalled();
    });
  });

  it('should display customer data in table format', async () => {
    (customerApi.fetchCustomers as jest.Mock).mockResolvedValue(mockPagedResponse);

    render(<CustomerList />);

    expect(await screen.findByText('John')).toBeInTheDocument();
    expect(screen.getByText('Doe')).toBeInTheDocument();
    expect(screen.getByText('Jane')).toBeInTheDocument();
    expect(screen.getByText('Smith')).toBeInTheDocument();
    expect(screen.getByText('1990-01-15')).toBeInTheDocument();
    expect(screen.getByText('1985-05-20')).toBeInTheDocument();
  });

  it('should show pagination information', async () => {
    (customerApi.fetchCustomers as jest.Mock).mockResolvedValue(mockPagedResponse);

    render(<CustomerList />);

    expect(await screen.findByText(/page 1 of 1/i)).toBeInTheDocument();
    expect(screen.getByText(/total.*2.*customers/i)).toBeInTheDocument();
  });

  it('should render pagination controls', async () => {
    (customerApi.fetchCustomers as jest.Mock).mockResolvedValue(mockPagedResponse);

    render(<CustomerList />);

    await waitFor(() => {
      expect(screen.getByRole('button', { name: /previous|prev/i })).toBeInTheDocument();
      expect(screen.getByRole('button', { name: /next/i })).toBeInTheDocument();
    });
  });

  it('should fetch next page when next button clicked', async () => {
    const user = userEvent.setup();
    const firstPageResponse: PagedResponse<any> = {
      customers: [
        {
          id: '123e4567-e89b-12d3-a456-426614174000',
          firstName: 'John',
          lastName: 'Doe',
          dateOfBirth: '1990-01-15',
        },
      ],
      page: 0,
      size: 20,
      totalElements: 25,
      totalPages: 2,
    };

    const secondPageResponse: PagedResponse<any> = {
      customers: [
        {
          id: '123e4567-e89b-12d3-a456-426614174002',
          firstName: 'Alice',
          lastName: 'Johnson',
          dateOfBirth: '1992-03-10',
        },
      ],
      page: 1,
      size: 20,
      totalElements: 25,
      totalPages: 2,
    };

    (customerApi.fetchCustomers as jest.Mock)
      .mockResolvedValueOnce(firstPageResponse)
      .mockResolvedValueOnce(secondPageResponse);

    render(<CustomerList />);

    await waitFor(() => {
      expect(screen.getByText('John')).toBeInTheDocument();
    });

    const nextButton = screen.getByRole('button', { name: /next/i });
    await user.click(nextButton);

    await waitFor(() => {
      expect(screen.getByText('Alice')).toBeInTheDocument();
    });
  });

  it('should fetch previous page when prev button clicked', async () => {
    const user = userEvent.setup();
    const firstPageResponse: PagedResponse<any> = {
      customers: [
        {
          id: '123e4567-e89b-12d3-a456-426614174000',
          firstName: 'John',
          lastName: 'Doe',
          dateOfBirth: '1990-01-15',
        },
      ],
      page: 0,
      size: 20,
      totalElements: 25,
      totalPages: 2,
    };

    const secondPageResponse: PagedResponse<any> = {
      customers: [
        {
          id: '123e4567-e89b-12d3-a456-426614174002',
          firstName: 'Alice',
          lastName: 'Johnson',
          dateOfBirth: '1992-03-10',
        },
      ],
      page: 1,
      size: 20,
      totalElements: 25,
      totalPages: 2,
    };

    (customerApi.fetchCustomers as jest.Mock)
      .mockResolvedValueOnce(firstPageResponse)
      .mockResolvedValueOnce(secondPageResponse)
      .mockResolvedValueOnce(firstPageResponse);

    render(<CustomerList />);

    await waitFor(() => {
      expect(screen.getByText('John')).toBeInTheDocument();
    });

    const nextButton = screen.getByRole('button', { name: /next/i });
    await user.click(nextButton);

    await waitFor(() => {
      expect(screen.getByText('Alice')).toBeInTheDocument();
    });

    const prevButton = screen.getByRole('button', { name: /previous|prev/i });
    await user.click(prevButton);

    await waitFor(() => {
      expect(screen.getByText('John')).toBeInTheDocument();
    });
  });

  it('should disable prev button on first page', async () => {
    (customerApi.fetchCustomers as jest.Mock).mockResolvedValue({
      ...mockPagedResponse,
      page: 0,
      totalPages: 2,
    });

    render(<CustomerList />);

    await waitFor(() => {
      const prevButton = screen.getByRole('button', { name: /previous|prev/i });
      expect(prevButton).toBeDisabled();
    });
  });

  it('should disable next button on last page', async () => {
    (customerApi.fetchCustomers as jest.Mock).mockResolvedValue({
      ...mockPagedResponse,
      page: 1,
      totalPages: 2,
    });

    render(<CustomerList />);

    await waitFor(() => {
      const nextButton = screen.getByRole('button', { name: /next/i });
      expect(nextButton).toBeDisabled();
    });
  });

  it('should display error message when fetch fails', async () => {
    const errorMessage = 'Failed to fetch customers';
    (customerApi.fetchCustomers as jest.Mock).mockRejectedValue(
      new Error(errorMessage)
    );

    render(<CustomerList />);

    expect(await screen.findByText(errorMessage)).toBeInTheDocument();
  });

  it('should show empty state when no customers exist', async () => {
    (customerApi.fetchCustomers as jest.Mock).mockResolvedValue({
      customers: [],
      page: 0,
      size: 20,
      totalElements: 0,
      totalPages: 0,
    });

    render(<CustomerList />);

    expect(await screen.findByText(/no customers found/i)).toBeInTheDocument();
  });
});
