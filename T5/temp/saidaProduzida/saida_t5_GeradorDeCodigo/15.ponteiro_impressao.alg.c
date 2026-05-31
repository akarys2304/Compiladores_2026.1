#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>

int main() {
	int x;
	int* endx;
	x = 0;
	printf("%d", x);
	printf(" e ");
	endx = &x;
	*endx = 1;
	printf("%d", x);
    return 0;
}
