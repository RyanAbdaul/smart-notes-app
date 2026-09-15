import { configureStore } from '@reduxjs/toolkit'
import { noteReducer } from '../features/notes/notesSlice'
import { authReducer } from '../features/auth/authSlice'


export const store = configureStore({
  reducer:{
    notes:noteReducer,
    auth:authReducer
  }
})
