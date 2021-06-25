package ast;

import util.Environment;
import util.Offset;
import util.SemanticError;
import util.SimpLanlib;

import java.util.ArrayList;

public class BinExpGtNode implements Node , Cloneable{

    private Node left;
    private Node right;
    private int effectDecFun;

    public BinExpGtNode(Node left, Node right) {
        this.left = left;
        this.right = right;
    }

    public Node getLeft() {
        return left;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public Node getRight() {
        return right;
    }

    public void setRight(Node right) {
        this.right = right;
    }

    public int getEffectDecFun() {
        return effectDecFun;
    }

    @Override
    public void setEffectDecFun(int effectDecFun) {
        this.effectDecFun = effectDecFun;
    }


    // toPrint, typeCheck, checkSemantics, checkEffects, codeGeneration

    @Override
    public String toPrint(String s) {
        return s + "Gt: " + left.toPrint(s + "") + ">" + right.toPrint(s + "") + "";

    }

    @Override
    public Node typeCheck() {
        if (!(SimpLanlib.isSubtype(left.typeCheck(), new IntTypeNode()) &&
                SimpLanlib.isSubtype(right.typeCheck(), new IntTypeNode()))) {
            System.out.println("error: bad operand types for binary operator '>'");
            System.exit(0);
        }
        return new BoolTypeNode();
    }


    // not used
    public int checkEffects(Environment env) {
        return 0;
    }

    @Override
    public ArrayList<SemanticError> checkSemantics(Environment env) {
        ArrayList<SemanticError> res = new ArrayList<SemanticError>();
        left.setEffectDecFun(this.effectDecFun);
        right.setEffectDecFun(this.effectDecFun);
        res.addAll(left.checkSemantics(env));
        res.addAll(right.checkSemantics(env));

        return res;
    }

    @Override
    public ArrayList<SemanticError> checkSemantics(Environment env, Offset offset) {
        return null;
    }

    @Override
    public Node clone() {
        try {
            BinExpGtNode cloned = (BinExpGtNode) super.clone();
            cloned.left = (Node) this.left.clone();
            cloned.right = (Node) this.right.clone();
            return cloned;
        } catch(CloneNotSupportedException e){
            return null;
        }
    }

    @Override
    public String codeGeneration() {
        String out = "";
        // x > y == !(x <= y)
        out+= left.codeGeneration();
        out+= right.codeGeneration();
        String false_branch_gt = SimpLanlib.freshLabel();
        String end_gt = SimpLanlib.freshLabel();
        return  out +                              // cgen of left and right
                // pop of cgen(stable, left) and cgen(stable, right) with beq
                "bleq " + false_branch_gt + "\n" +      // //pop two values x,y on top of the stack and jump if right>=left
                "push 1\n" +                        // true in the stack
                "b " + end_gt + "\n" +             // jump end_if
                false_branch_gt + ":\n" +              // false_branch_gt:
                "push 0\n" +                        // false in the stack
                end_gt + ":\n";                    // end_gy :
    }

}
