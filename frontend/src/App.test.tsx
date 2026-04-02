import React from 'react';
import { render, screen } from '@testing-library/react';
import App from './App';

jest.mock('./services/customerApi');

test('renders customer management header', () => {
  render(<App />);
  const headerElement = screen.getByText(/customer management/i);
  expect(headerElement).toBeInTheDocument();
});
