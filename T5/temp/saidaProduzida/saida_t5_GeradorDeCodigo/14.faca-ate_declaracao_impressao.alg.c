#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>

int main() {
	int i;
	i = 1;
	do {
		printf("%d", i);
	printf("\n");
		i = i + 1;
	} while (!(i == 6));
    return 0;
}
