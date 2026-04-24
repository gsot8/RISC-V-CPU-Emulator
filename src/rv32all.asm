lui t1, 1
addi s0, s1, 7
slti a1, zero, 0
sltiu t2, ra, 0
xori t2, ra, 0
ori t3, t2, 7
andi a2, t3, 7
slli a3, a2, 7
srli a3, a2, 7
srai a4, a3, 7
add s2, a4, a3
sub s3, s2, a4
sll s4, s3, zero
slt a5, a0, s1
sltu a6, ra, s0
fence.i
ecall
ebreak
lb t4, s3, 0
lh t5, zero, 0
lw t6, zero 1024
lbu t6, zero, 32
lhu t6, zero, 0
sb t6, zero, 10
sh s5, zero, 0
sw s6, zero, 0
mulh s5, s5, s6
mulhsu s0, s5, zero
mulhu s5, s5, a1
div t1, t1, t1
divu t1, t1, t1
rem t2, t1, t1
remu t2, t1, t1
beq zero, a6, 4
bne a6, s5, 4
blt s5, t5, 4
bge a5, a6, 4
bgeu t1, ra, 4
mul t5, s5, s6
xor s5, a2, s2
srl a5, a6, zero
sra a5, s5, a6
or s5, s1, a1
and s5, s1, a1
fence iorw, iorw
jalr zero, ra, -8
jal zero, 4
auipc ra, 0