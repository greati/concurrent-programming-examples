#ifndef __MAT_H__
#define __MAT_H__

#include <iostream>
#include <thread>
#include <vector>
#include <stdexcept>
#include <sstream>
#include <fstream>

namespace Mat {
	extern const int min_n;
	extern const int max_n;
	typedef std::vector<std::vector<int>> Matrix;
	// mxn nxp

	void compute_mult_line(const Matrix& A, const Matrix& B, Matrix& C, 
		const int & i);
	void sequential_mult(const Matrix& A, const Matrix& B, Matrix& C);
	void concurrent_mult(const Matrix& A, const Matrix& B, Matrix& C);

	void read_arguments(const char* n_arg, const char* method_arg, int& n, char& method);
	void read_matrix(std::string filename, Matrix& matrix);
	void print_matrix(const Matrix& matrix);
}

#endif