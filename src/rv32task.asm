lui   a0, 0
lui t5, 0
fence, ior, iorw
addi t6, zero, 32
beq t5, t6, 0x14
lb t0, a0, 16
addi a0, a0, 1024
addi t5, t5, 8
jal zero, -0x10
addi a0, a0 1024
lb t0, a0, 16
addi t6, t6, 24
beq t5, t6, 0x20
lb t0, a0, 17
lb t1, a0, 16
lb t2, a0, 16
lb t3, a0, 16
lb t4, a0, 16
addi t5, t5, 6
jal zero, -0x1c
lb t0, a0, 17
lb t1, a0, 16
lb t2, a0, 16
lb t3, a0, 16
lb t4, a0, 16
lb t0, a0, 17
lb t1, a0, 16
lb t2, a0, 16
lb t3, a0, 16
lb t4, a0, 16
lb t0, a0, 17
lb t1, a0, 16
lb t2, a0, 16
lb t3, a0, 16
lb t4, a0, 16
addi zero, zero, 0
addi ra, ra, 0
addi sp, sp, 0
addi gp, gp, 0
addi tp, tp, 0
addi t0, t0, 0
addi t1, t1, 0
addi t2, t2, 0
addi s0, s0, 0
addi s1, s1, 0
addi a0, a0, 0
addi a1, a1, 0
addi a2, a2, 0
addi a3, a3, 0
addi a4, a4, 0
addi a5, a5, 0
addi a6, a6, 0
addi a7, a7, 0
addi s2, s2, 0
addi s3, s3, 0
addi s4, s4, 0
addi s5, s5, 0
addi s6, s6, 0
addi s7, s7, 0
addi s8, s8, 0
addi s9, s9, 0
addi s10, s10, 0
addi s11, s11, 0
addi t3, t3, 0
addi t4, t4, 0
addi t5, t5, 0
addi t6, t6, 0
addi a0, zero, 2
beq a0, a1, 0xC
addi a1, a1, 1
jal zero, -0x8
lui a0, 0



