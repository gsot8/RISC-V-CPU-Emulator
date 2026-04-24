lui t0, 0x100
srli t0, t0, 12
lui t2, 0x1800
srli t2, t2, 12

addi t4, zero, 0

addi t6, zero, 64
beq t4, t6, 0x88


addi t3, zero, 0

addi t6, zero, 60
beq t3, t6, 0x68


lui t1, 0x900
srli t1, t1, 12
addi s1, zero, 0

addi t5, zero, 0

addi t6, zero, 32
beq t5, t6, 0x34


addi t6, t5, 0
add t6, t6, t0
lb s2, 0, t6

addi t6, t3, 0
slli t6, t6, 1
add t6, t6, t1
lh s3, 0, t6

mul t6, s2, s3
add s1, s1, t6


addi t1, t1, 120

addi t5, t5, 1
jal zero, -0x34


addi zero, zero, 0



addi t6, t3, 0
slli t6, t6, 2
add t6, t6, t2
sw s1, 0, t6

addi t3, t3, 1
jal zero, -0x68


addi zero, zero, 0

addi t0, t0, 32

addi t2, t2, 240

addi t4, t4, 1
jal zero, -0x88


addi zero, zero, 0

jalr zero, ra, 0
