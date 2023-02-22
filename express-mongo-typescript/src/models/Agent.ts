import mongoose,{ Document, now, Schema } from  "mongoose";

export interface IAgent {
    agentemail: String;
    password: String;
}

export interface IAgentModel extends IAgent,Document{}

const AgentSchema: Schema = new Schema(
    {
        agentname: { type: String, required: true },
        agentmobile: { type: String, required: true },
        workpincode: { type: String, required: true },
        agentemail: { type: String, required: true, unique: true },
        password: { type: String, required: true }
    },
    {
        // versionKey: false
        timestamps: true
    }
);

export default mongoose.model<IAgentModel>('Agent', AgentSchema);
