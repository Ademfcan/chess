import os
import fnmatch

def count_lines_in_file(file_path):
    """Count the number of lines in a given file."""
    with open(file_path, 'r', encoding='utf-8', errors='ignore') as file:
        return sum(1 for line in file if line.strip())  # Count non-empty lines

def count_lines_in_directory(directory):
    """Recursively count lines in .java and .fxml files within the given directory."""
    total_lines = 0

    for root, dirs, files in os.walk(directory):
        for filename in fnmatch.filter(files, '*.java') + fnmatch.filter(files, '*.fxml'):
            file_path = os.path.join(root, filename)
            lines_count = count_lines_in_file(file_path)
            print(f"{file_path}: {lines_count} lines")
            total_lines += lines_count

    return total_lines

if __name__ == "__main__":
    # Specify the directory to search
    directory_to_search = input("Enter the directory to search: ")
    total = count_lines_in_directory(directory_to_search)
    print(f"\nTotal lines in .java and .fxml files: {total}")