import React, { useState } from 'react';
import { Customer, CustomerRequest } from '../types/Customer';
import { createCustomer } from '../services/customerApi';

interface CustomerFormProps {
  onSuccess: (customer: Customer) => void;
}

interface FormErrors {
  firstName?: string;
  lastName?: string;
  dateOfBirth?: string;
}

const CustomerForm: React.FC<CustomerFormProps> = ({ onSuccess }) => {
  const [formData, setFormData] = useState<CustomerRequest>({
    firstName: '',
    lastName: '',
    dateOfBirth: '',
  });

  const [errors, setErrors] = useState<FormErrors>({});
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [apiError, setApiError] = useState<string | null>(null);

  const validateForm = (): FormErrors => {
    const newErrors: FormErrors = {};

    if (!formData.firstName.trim()) {
      newErrors.firstName = 'First name is required';
    } else if (!/^[a-zA-Z\s'-]+$/.test(formData.firstName)) {
      newErrors.firstName = 'First name contains invalid characters';
    }

    if (!formData.lastName.trim()) {
      newErrors.lastName = 'Last name is required';
    } else if (!/^[a-zA-Z\s'-]+$/.test(formData.lastName)) {
      newErrors.lastName = 'Last name contains invalid characters';
    }

    if (!formData.dateOfBirth) {
      newErrors.dateOfBirth = 'Date of birth is required';
    } else if (new Date(formData.dateOfBirth) >= new Date()) {
      newErrors.dateOfBirth = 'Date of birth must be in the past';
    }

    return newErrors;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    const validationErrors = validateForm();
    setErrors(validationErrors);

    if (Object.keys(validationErrors).length > 0) {
      return;
    }

    setIsSubmitting(true);
    setApiError(null);

    try {
      const customer = await createCustomer(formData);
      setFormData({ firstName: '', lastName: '', dateOfBirth: '' });
      setErrors({});
      onSuccess(customer);
    } catch (error) {
      setApiError(error instanceof Error ? error.message : 'Failed to create customer');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  return (
    <form onSubmit={handleSubmit}>
      <div>
        <label htmlFor="firstName">First Name</label>
        <input
          id="firstName"
          name="firstName"
          type="text"
          value={formData.firstName}
          onChange={handleChange}
        />
        {errors.firstName && <div>{errors.firstName}</div>}
      </div>

      <div>
        <label htmlFor="lastName">Last Name</label>
        <input
          id="lastName"
          name="lastName"
          type="text"
          value={formData.lastName}
          onChange={handleChange}
        />
        {errors.lastName && <div>{errors.lastName}</div>}
      </div>

      <div>
        <label htmlFor="dateOfBirth">Date of Birth</label>
        <input
          id="dateOfBirth"
          name="dateOfBirth"
          type="date"
          value={formData.dateOfBirth}
          onChange={handleChange}
        />
        {errors.dateOfBirth && <div>{errors.dateOfBirth}</div>}
      </div>

      {apiError && <div>{apiError}</div>}

      <button type="submit" disabled={isSubmitting}>
        {isSubmitting ? 'Submitting...' : 'Create Customer'}
      </button>
    </form>
  );
};

export default CustomerForm;
