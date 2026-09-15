import { useEffect, useState } from "react";
import {  useSelector } from "react-redux";
import CheckBox from "../common/CheckBox";

const NoteEditor = () => {
  const { data, activeNoteId } = useSelector((state) => state.notes);
  const currentNote = data.find((ele) => ele.id === activeNoteId);
  const [title, setTitle] = useState("");
  const [body, setBody] = useState("");

  useEffect(() => {
    if (currentNote) {
      setTitle(currentNote.title || "");
      setBody(currentNote.body || "");
    } else {
      setTitle("");
      setBody("");
    }
  }, [currentNote]);
  return (
    <section className="p-2 ">
      <textarea
        style={{ fieldSizing: "content" }}
        type="text"
        className="outline-hidden resize-none overflow-hidden w-full h-full text-3xl font-bold mb-3"
        value={title}
        onChange={(e) => setTitle(e.target.value)}
        placeholder="Heading"
      />
      <textarea
        style={{ fieldSizing: "content" }}
        type="text"
        className="outline-hidden resize-none overflow-hidden w-full h-full text-lg "
        value={body}
        onChange={(e) => setBody(e.target.value)}
        placeholder="Details"
      />
        <CheckBox  />
    </section>
  );
};

export default NoteEditor;
