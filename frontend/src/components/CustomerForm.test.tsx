import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import CustomerForm from './CustomerForm';
import * as customerApi from '../services/customerApi';

jest.mock('../services/customerApi');

describe('CustomerForm', () => {
  const mockOnSuccess = jest.fn();

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render all form fields', () => {
    render(<CustomerForm onSuccess={mockOnSuccess} />);

    expect(screen.getByLabelText(/first name/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/last name/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/date of birth/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /submit|create/i })).toBeInTheDocument();
  });

  it('should show validation errors for empty fields', async () => {
    const user = userEvent.setup();
    render(<CustomerForm onSuccess={mockOnSuccess} />);

    const submitButton = screen.getByRole('button', { name: /submit|create/i });
    await user.click(submitButton);

    expect(await screen.findByText(/first name is required/i)).toBeInTheDocument();
    expect(await screen.findByText(/last name is required/i)).toBeInTheDocument();
    expect(await screen.findByText(/date of birth is required/i)).toBeInTheDocument();
  });

  it('should show validation error for future date of birth', async () => {
    const user = userEvent.setup();
    render(<CustomerForm onSuccess={mockOnSuccess} />);

    const firstNameInput = screen.getByLabelText(/first name/i);
    const lastNameInput = screen.getByLabelText(/last name/i);
    const dateInput = screen.getByLabelText(/date of birth/i);
    const submitButton = screen.getByRole('button', { name: /submit|create/i });

    await user.type(firstNameInput, 'John');
    await user.type(lastNameInput, 'Doe');
    await user.type(dateInput, '2030-01-01');
    await user.click(submitButton);

    expect(await screen.findByText(/date of birth must be in the past/i)).toBeInTheDocument();
  });

  it('should show validation error for invalid name characters', async () => {
    const user = userEvent.setup();
    render(<CustomerForm onSuccess={mockOnSuccess} />);

    const firstNameInput = screen.getByLabelText(/first name/i);
    const submitButton = screen.getByRole('button', { name: /submit|create/i });

    await user.type(firstNameInput, 'John123');
    await user.click(submitButton);

    expect(await screen.findByText(/invalid characters/i)).toBeInTheDocument();
  });

  it('should submit form with valid data and call API', async () => {
    const user = userEvent.setup();
    const mockCustomer = {
      id: '123e4567-e89b-12d3-a456-426614174000',
      firstName: 'John',
      lastName: 'Doe',
      dateOfBirth: '1990-01-15',
    };

    (customerApi.createCustomer as jest.Mock).mockResolvedValue(mockCustomer);

    render(<CustomerForm onSuccess={mockOnSuccess} />);

    const firstNameInput = screen.getByLabelText(/first name/i);
    const lastNameInput = screen.getByLabelText(/last name/i);
    const dateInput = screen.getByLabelText(/date of birth/i);
    const submitButton = screen.getByRole('button', { name: /submit|create/i });

    await user.type(firstNameInput, 'John');
    await user.type(lastNameInput, 'Doe');
    await user.type(dateInput, '1990-01-15');
    await user.click(submitButton);

    await waitFor(() => {
      expect(customerApi.createCustomer).toHaveBeenCalledWith({
        firstName: 'John',
        lastName: 'Doe',
        dateOfBirth: '1990-01-15',
      });
    });
  });

  it('should call onSuccess callback after successful submission', async () => {
    const user = userEvent.setup();
    const mockCustomer = {
      id: '123e4567-e89b-12d3-a456-426614174000',
      firstName: 'John',
      lastName: 'Doe',
      dateOfBirth: '1990-01-15',
    };

    (customerApi.createCustomer as jest.Mock).mockResolvedValue(mockCustomer);

    render(<CustomerForm onSuccess={mockOnSuccess} />);

    const firstNameInput = screen.getByLabelText(/first name/i);
    const lastNameInput = screen.getByLabelText(/last name/i);
    const dateInput = screen.getByLabelText(/date of birth/i);
    const submitButton = screen.getByRole('button', { name: /submit|create/i });

    await user.type(firstNameInput, 'John');
    await user.type(lastNameInput, 'Doe');
    await user.type(dateInput, '1990-01-15');
    await user.click(submitButton);

    await waitFor(() => {
      expect(mockOnSuccess).toHaveBeenCalledWith(mockCustomer);
    });
  });

  it('should display API error message on failure', async () => {
    const user = userEvent.setup();
    const errorMessage = 'Date of birth must be in the past';

    (customerApi.createCustomer as jest.Mock).mockRejectedValue(
      new Error(errorMessage)
    );

    render(<CustomerForm onSuccess={mockOnSuccess} />);

    const firstNameInput = screen.getByLabelText(/first name/i);
    const lastNameInput = screen.getByLabelText(/last name/i);
    const dateInput = screen.getByLabelText(/date of birth/i);
    const submitButton = screen.getByRole('button', { name: /submit|create/i });

    await user.type(firstNameInput, 'John');
    await user.type(lastNameInput, 'Doe');
    await user.type(dateInput, '2030-01-01');
    await user.click(submitButton);

    expect(await screen.findByText(errorMessage)).toBeInTheDocument();
  });

  it('should show loading state during submission', async () => {
    const user = userEvent.setup();
    let resolvePromise: (value: any) => void;
    const promise = new Promise((resolve) => {
      resolvePromise = resolve;
    });

    (customerApi.createCustomer as jest.Mock).mockReturnValue(promise);

    render(<CustomerForm onSuccess={mockOnSuccess} />);

    const firstNameInput = screen.getByLabelText(/first name/i);
    const lastNameInput = screen.getByLabelText(/last name/i);
    const dateInput = screen.getByLabelText(/date of birth/i);
    const submitButton = screen.getByRole('button', { name: /submit|create/i });

    await user.type(firstNameInput, 'John');
    await user.type(lastNameInput, 'Doe');
    await user.type(dateInput, '1990-01-15');
    await user.click(submitButton);

    expect(submitButton).toBeDisabled();

    resolvePromise!({
      id: '123e4567-e89b-12d3-a456-426614174000',
      firstName: 'John',
      lastName: 'Doe',
      dateOfBirth: '1990-01-15',
    });

    await waitFor(() => {
      expect(submitButton).not.toBeDisabled();
    });
  });

  it('should clear form after successful submission', async () => {
    const user = userEvent.setup();
    const mockCustomer = {
      id: '123e4567-e89b-12d3-a456-426614174000',
      firstName: 'John',
      lastName: 'Doe',
      dateOfBirth: '1990-01-15',
    };

    (customerApi.createCustomer as jest.Mock).mockResolvedValue(mockCustomer);

    render(<CustomerForm onSuccess={mockOnSuccess} />);

    const firstNameInput = screen.getByLabelText(/first name/i) as HTMLInputElement;
    const lastNameInput = screen.getByLabelText(/last name/i) as HTMLInputElement;
    const dateInput = screen.getByLabelText(/date of birth/i) as HTMLInputElement;
    const submitButton = screen.getByRole('button', { name: /submit|create/i });

    await user.type(firstNameInput, 'John');
    await user.type(lastNameInput, 'Doe');
    await user.type(dateInput, '1990-01-15');
    await user.click(submitButton);

    await waitFor(() => {
      expect(firstNameInput.value).toBe('');
      expect(lastNameInput.value).toBe('');
      expect(dateInput.value).toBe('');
    });
  });
});
