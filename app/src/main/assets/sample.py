#!/usr/bin/env python3
"""
This is a comprehensive Python sample file
demonstrating enhanced syntax highlighting

Features:
- Single-line comments
- Multi-line docstrings
- Python keywords and types
- String literals
- Function and class definitions
"""

# Import statements
import os
import sys
from typing import List, Dict, Optional, Union
from dataclasses import dataclass

# Global variables
VERSION = "1.0.0"
DEBUG_MODE = True

class Calculator:
    """
    A simple calculator class demonstrating Python syntax highlighting
    """
    
    def __init__(self, name: str = "Calculator"):
        self.name = name
        self.history: List[str] = []
    
    def add(self, a: Union[int, float], b: Union[int, float]) -> Union[int, float]:
        """Add two numbers and return the result"""
        result = a + b
        self.history.append(f"{a} + {b} = {result}")
        return result
    
    def subtract(self, a: Union[int, float], b: Union[int, float]) -> Union[int, float]:
        """Subtract b from a and return the result"""
        result = a - b
        self.history.append(f"{a} - {b} = {result}")
        return result
    
    def get_history(self) -> List[str]:
        """Return calculation history"""
        return self.history.copy()

@dataclass
class Person:
    """Data class representing a person"""
    name: str
    age: int
    email: Optional[str] = None
    
    def is_adult(self) -> bool:
        """Check if person is an adult"""
        return self.age >= 18

def main():
    """Main function demonstrating various Python features"""
    
    # String literals
    single_quoted = 'This is a single-quoted string'
    double_quoted = "This is a double-quoted string"
    triple_quoted = """This is a triple-quoted
    multi-line string"""
    
    # List and dictionary comprehensions
    numbers = [1, 2, 3, 4, 5]
    squares = [x**2 for x in numbers if x % 2 == 0]
    
    # Dictionary with different value types
    config = {
        "host": "localhost",
        "port": 8080,
        "debug": True,
        "timeout": 30.5
    }
    
    # Control structures
    if DEBUG_MODE:
        print("Debug mode is enabled")
        for i, num in enumerate(numbers):
            if num % 2 == 0:
                print(f"Even number {num} at index {i}")
            else:
                print(f"Odd number {num} at index {i}")
    
    # Exception handling
    try:
        calculator = Calculator("MyCalc")
        result = calculator.add(10, 5)
        print(f"10 + 5 = {result}")
        
        # This will raise an exception
        division_result = 10 / 0
    except ZeroDivisionError as e:
        print(f"Error: {e}")
    except Exception as e:
        print(f"Unexpected error: {e}")
    finally:
        print("Exception handling completed")
    
    # Context manager
    with open("temp.txt", "w") as f:
        f.write("Hello, Python!")
    
    # Lambda functions
    multiply = lambda x, y: x * y
    print(f"Lambda result: {multiply(3, 4)}")
    
    # Generator function
    def fibonacci(n: int):
        """Generate Fibonacci numbers up to n"""
        a, b = 0, 1
        for _ in range(n):
            yield a
            a, b = b, a + b
    
    # Async function example
    async def async_example():
        """Async function example"""
        await asyncio.sleep(1)
        return "Async operation completed"
    
    # List operations
    fruits = ["apple", "banana", "cherry"]
    fruits.append("orange")
    fruits.extend(["grape", "kiwi"])
    
    # Set operations
    unique_numbers = {1, 2, 2, 3, 3, 4}  # Duplicates are removed
    print(f"Unique numbers: {unique_numbers}")
    
    # Tuple unpacking
    coordinates = (10, 20)
    x, y = coordinates
    
    # String formatting
    name = "Alice"
    age = 30
    formatted_string = f"{name} is {age} years old"
    print(formatted_string)
    
    # Boolean operations
    is_valid = True and not False
    is_invalid = False or (True and False)
    
    # None check
    value = None
    if value is None:
        print("Value is None")
    
    # Return statement
    return "Main function completed successfully"

# Main execution
if __name__ == "__main__":
    try:
        result = main()
        print(result)
    except KeyboardInterrupt:
        print("\nProgram interrupted by user")
    except Exception as e:
        print(f"Program error: {e}")
    finally:
        print("Program finished")
