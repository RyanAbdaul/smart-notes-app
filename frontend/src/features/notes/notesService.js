import { createAsyncThunk } from "@reduxjs/toolkit";
import apiClient from "../../api/apiClient"


export const getAllNotes=createAsyncThunk("notes/getAllNotes",async()=>{
    const {data}= await apiClient.get('/posts') ;
    return data
})
export const getToDos=createAsyncThunk("notes/getToDos",async()=>{
    const {data}= await apiClient.get('/todos') ;
    return data
})

