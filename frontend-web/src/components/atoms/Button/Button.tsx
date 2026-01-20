import type { ButtonHTMLAttributes, ReactNode } from 'react';
import './Button.css';

export interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  children: ReactNode;
}

export const Button = ({ children, className = '', ...rest }: ButtonProps) => {
  return (
    <button className={`sl-button ${className}`} {...rest}>
      {children}
    </button>
  );
};

export default Button;
