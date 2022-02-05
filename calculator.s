#
# Usage: ./calculator <op> <arg1> <arg2>
# <op> should be written +, -, "*", or / as shown
# arg1 and arg2 should be integers
#

.global main

.text

# int main(int argc, char argv[][])
main:
  # Function prologue:
  enter $0, $0
       
  # Variable mappings:
  # op -> %r12
  # arg1 -> %r13
  # arg2 -> %r14
  movq 8(%rsi), %r12  # op = argv[1]
  movq 16(%rsi), %r13 # arg1 = argv[2]
  movq 24(%rsi), %r14 # arg2 = argv[3]

  # Updated Variable Mappings: 
  # op -> %r8b
  # arg1 -> %r13
  # arg2 -> %r14
  # Converting op into an 8-bit register

  # Converting 1st operand to long int
  mov %r13, %rdi  
  call atol
  mov %rax, %r13
  # Converting 2nd operand to long int
  mov %r14, %rdi
  call atol
  mov %rax, %r14
 
  mov (%r12), %r8b
  # Function body:
  # if (op = +)
  #   printf(arg1 + arg2)
  # else if (op = -)
  #   printf(arg1 - arg2)
  # else if (op = "*")
  #   printf(arg1 * arg2)
  # else if (op = /)
  #   printf(arg1 / arg2) (no remainder)
  # else printf("Unknown Operation")
  cmp $'+', %r8b
  je add_it
  cmp $'-', %r8b
  je sub_it
  cmp $'*', %r8b
  je mult_it
  cmp $'/', %r8b
  je div_it
  mov $error, %rdi
  call printf
  leave
  ret

# add function
add_it:
  addq %r13, %r14
  jmp print_format

# subtract function
sub_it:
  subq %r14, %r13
  mov %r13, %r14
  jmp print_format

# multiply function
mult_it:
  imulq %r13, %r14
  jmp print_format

# divide function
div_it:  
  mov %r13, %rax
  cqto
  idivq %r14
  mov %rax, %r14
  jmp print_format

# Function epilogue:
# print and return function
print_format:
  mov %r14, %rsi
  mov $format, %rdi
  mov $0, %al
  call printf
  leave
  ret

# Start of the data section
.data

# long format
format: 
  .asciz "%ld\n"

# error message
error:
  .asciz "Unknown operation\n"



