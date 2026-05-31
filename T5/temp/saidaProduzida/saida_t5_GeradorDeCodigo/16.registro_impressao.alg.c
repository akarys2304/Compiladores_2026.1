#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>

int main() {
	struct {
		char nome[80];
		int idade;
	} reg;
	strcpy(reg.nome, "Maria");
	reg.idade = 24;
	printf("%s", reg.nome);
	printf(" tem ");
	printf("%d", reg.idade);
	printf(" anos");
    return 0;
}
