package com.example.beesness.database.repositories;

import com.example.beesness.database.interfaces.FirestoreCallback;
import com.example.beesness.database.interfaces.IMemberRepository;
import com.example.beesness.models.Member;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class MemberRepository implements IMemberRepository {
    private static MemberRepository instance;
    private CollectionReference ref;

    private MemberRepository(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ref = db.collection("members");
    }

    public static synchronized MemberRepository getInstance(){
        if(instance == null) {
            instance = new MemberRepository();
        }
        return instance;
    }
    @Override
    public void add(Member member, FirestoreCallback<Member> callback) {
        ref.add(member).addOnSuccessListener(documentReference -> {
            member.setId(ref.getId());
            callback.onSuccess(member);
        }).addOnFailureListener(callback::onFailure);
    }

    @Override
    public void getAll(FirestoreCallback<List<Member>> callback) {
        ref.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Member> memberList = queryDocumentSnapshots.toObjects(Member.class);
            callback.onSuccess(memberList);
        }).addOnFailureListener(callback::onFailure);
    }

    @Override
    public void getById(String id, FirestoreCallback<Member> callback) {
        ref.document(id).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Member member = documentSnapshot.toObject(Member.class);
                    callback.onSuccess(member);
                }).addOnFailureListener(callback::onFailure);
    }

    @Override
    public void update(String id, Member member, FirestoreCallback<Void> callback) {
        ref.document(id).set(member)
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(callback::onFailure);
    }

    @Override
    public void delete(String id, FirestoreCallback<Void> callback) {
        ref.document(id).delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(callback::onFailure);
    }
}
