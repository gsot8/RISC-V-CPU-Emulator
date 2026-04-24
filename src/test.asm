addi ra, zero, 1
addi sp, zero, 1
addi gp, zero, 1
addi tp, zero, 1
addi t0, zero, 1
addi t1, zero, 1
addi t2, zero, 1
addi s0, zero, 1
addi s1, zero, 1
addi a0, zero, 1
addi a1, zero, 1
addi a2, zero, 1
addi a3, zero, 1
addi a4, zero, 1
addi a5, zero, 1
addi a6, zero, 1
addi a7, zero, 1
addi s2, zero, 1
addi s3, zero, 1
addi s4, zero, 1
addi s5, zero, 1
addi s6, zero, 1
addi s7, zero, 1
addi s8, zero, 1
addi s9, zero, 1
addi s10, zero, 1
addi s11, zero, 1
addi t3, zero, 1
addi t4, zero, 1
addi t5, zero, 1
addi t6, zero, 1
add  t0, t1, t2
sub  t1, t2, t3
mul  a0, a1, a2
mulh a3, a4, a5
mulhu s0, s1, s2
mulhsu s3, s4, s5
rem  s6, s7, s8
remu s9, s10, s11
slt  t3, t4, t5
sltu t4, t5, t6
and  t5, t6, zero
or   t6, t0, t1
xor  t0, t1, t2
sll  a0, a1, a2
srl  a3, a4, a5
sra  a6, a7, s0
addi s1, s2, 123
slti s3, s4, -5
sltiu s5, s6, 42
andi s7, s8, 0xFF
ori  s9, s10, 0x0F
xori s11, t0, 0xAA
slli t1, t2, 3
srli t3, t4, 2
srai t5, t6, 4
lui  t0, 0x12345
auipc t1, 0x1000
jal  zero, -1
jalr t2, ra, 4
fence
ecall
ebreak
sw t0, 0x100, zero
sh t1, 0x104, zero
sb t2, 0x106, zero